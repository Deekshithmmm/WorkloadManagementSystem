# task_receiver.py
from flask import Flask, request, jsonify
import subprocess
import threading
import requests
import os

app = Flask(__name__)

@app.route('/execute', methods=['POST'])
def execute_task():
    data        = request.json
    task_id     = data.get('taskId', '?')
    task_name   = data.get('taskName', '')
    origin_ip   = data.get('originIp', '')

    print(f"\n📥 Task #{task_id} received from {origin_ip}")
    print(f"   Name: {task_name}")

    def run():
        try:
            # ── Step 1: Fetch the app list from the source laptop ──────────────
            print(f"   Fetching app list from source: {origin_ip}")
            resp = requests.get(
                f"http://{origin_ip}:9090/current-tasks",
                timeout=5
            )
            resp.raise_for_status()
            tasks = resp.json().get("tasks", [])

            if not tasks:
                print("   ⚠️  No running apps found on source laptop")
                return

            print(f"   Found {len(tasks)} app(s) to transfer:")

            # ── Step 2: Open each app on this laptop ───────────────────────────
            success_count = 0
            for t in tasks:
                command  = t.get("command", "")
                app_type = t.get("appType", "unknown")
                content  = t.get("content", "")

                print(f"   ▶ Opening: {app_type} — {command}")

                if command:
                    subprocess.Popen(command, shell=True)
                    print(f"   ✅ {app_type} launched successfully")
                    success_count += 1
                else:
                    print(f"   ⚠️  Skipped {app_type} — no command provided")

            # ── Step 3: Log the transfer ───────────────────────────────────────
            os.makedirs("C:\\wms_tasks", exist_ok=True)
            with open("C:\\wms_tasks\\task_log.txt", "a") as f:
                f.write(
                    f"Task #{task_id} received from {origin_ip} — "
                    f"opened {success_count}/{len(tasks)} app(s)\n"
                )
            print(f"   📝 Log updated ({success_count}/{len(tasks)} apps launched)")

        except requests.exceptions.ConnectionError:
            print(f"   ❌ Could not connect to source at {origin_ip}:9090 — is it online?")
        except requests.exceptions.Timeout:
            print(f"   ❌ Timed out waiting for source at {origin_ip}:9090")
        except requests.exceptions.HTTPError as e:
            print(f"   ❌ Source returned error: {e}")
        except Exception as e:
            print(f"   ❌ Unexpected failure: {e}")

    threading.Thread(target=run, daemon=True).start()
    return jsonify({"status": "accepted", "taskId": task_id}), 200


@app.route('/current-tasks', methods=['GET'])
def get_current_tasks():
    """
    Source laptop exposes this endpoint.
    Target calls it to fetch the list of apps currently running on source.
    """
    try:
        from task_capture import capture_current_work
        tasks = capture_current_work()
        print(f"📤 Sharing {len(tasks)} running app(s) with target")
        return jsonify({"tasks": tasks}), 200
    except ImportError:
        print("❌ task_capture module not found")
        return jsonify({"tasks": [], "error": "task_capture module missing"}), 500
    except Exception as e:
        print(f"❌ Capture failed: {e}")
        return jsonify({"tasks": [], "error": str(e)}), 500


@app.route('/health', methods=['GET'])
def health_check():
    """Optional — lets the Java service verify the agent is alive."""
    return jsonify({"status": "ok"}), 200


if __name__ == '__main__':
    print("🟢 Task receiver agent started — listening on port 9090")
    app.run(host='0.0.0.0', port=9090)