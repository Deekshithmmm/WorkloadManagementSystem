import cv2
import time
import requests
import socket
import uuid
import threading
from task_receiver import app as receiver_app
from system_stats import get_system_stats, get_ip
from presence_detector import detect_employee_presence
from activity_monitor import analyze_activity

BACKEND_URL = "http://localhost:8080"
NODE_ID = socket.gethostname() + "-" + str(uuid.uuid4())[:6]
EMPLOYEE_NAME = "John Doe"  # configurable
def start_receiver():
    receiver_app.run(port=9090, use_reloader=False)

threading.Thread(target=start_receiver, daemon=True).start()
print("📡 Task receiver listening on port 9090")


def send_heartbeat(data):
    try:
        requests.post(f"{BACKEND_URL}/api/nodes/heartbeat", json=data, timeout=3)
    except Exception as e:
        print(f"⚠️  Backend unreachable: {e}")

def send_activity_alert(activity_type, description):
    payload = {
        "nodeId": NODE_ID,
        "employeeName": EMPLOYEE_NAME,
        "activityType": activity_type,
        "description": description
    }
    try:
        requests.post(f"{BACKEND_URL}/api/monitoring/activity", json=payload, timeout=3)
    except Exception as e:
        print(f"⚠️  Could not report activity: {e}")

def main():
    cap = cv2.VideoCapture(0)
    print(f"🟢 Agent started: {NODE_ID}")

    distraction_counter = 0

    while True:
        ret, frame = cap.read()
        if not ret:
            time.sleep(2)
            continue

        # 1. System stats
        stats = get_system_stats()

        # 2. Employee presence
        present = detect_employee_presence(frame)

        # 3. Activity analysis
        if present:
            activity, desc = analyze_activity(frame)
        else:
            activity, desc = "AWAY", "Employee not at desk"

        active = activity == "WORKING"

        # 4. Build payload
        payload = {
            "nodeId": NODE_ID,
            "ipAddress": get_ip(),
            "employeeName": EMPLOYEE_NAME,
            "cpuUsage": stats["cpuUsage"],
            "memoryUsage": stats["memoryUsage"],
            "availableMemoryGB": stats["availableMemoryGB"],
            "activeTasks": stats["activeTasks"],
            "employeePresent": present,
            "employeeActive": active,
            "currentActivity": activity
        }

        send_heartbeat(payload)
        print(f"📡 {NODE_ID} | CPU: {stats['cpuUsage']}% | "
              f"Present: {present} | Activity: {activity}")

        # 5. Trigger alert if distracted multiple times in a row
        if activity in ("PHONE", "SLEEPING", "AWAY"):
            distraction_counter += 1
            if distraction_counter >= 3:
                send_activity_alert(activity, desc)
                print(f"🚨 ALERT: {desc}")
                distraction_counter = 0
        else:
            distraction_counter = 0

        # Optional preview window
        cv2.putText(frame, f"{activity}", (10, 30),
                    cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 255, 0), 2)
        cv2.imshow("WMS Agent", frame)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

        time.sleep(5)

    cap.release()
    cv2.destroyAllWindows()

if __name__ == "__main__":
    main()