import cv2
import mediapipe as mp
from ultralytics import YOLO

# YOLOv8 pretrained model (detects phone, person, etc.)
yolo_model = YOLO("yolov8n.pt")

mp_face_mesh = mp.solutions.face_mesh
face_mesh = mp_face_mesh.FaceMesh(min_detection_confidence=0.5)

def detect_phone_usage(frame):
    """Returns True if a cell phone is detected."""
    results = yolo_model(frame, verbose=False)
    for r in results:
        for cls in r.boxes.cls:
            label = yolo_model.names[int(cls)]
            if label == "cell phone":
                return True
    return False

def detect_drowsiness(frame):
    """Returns True if eyes appear closed (simple EAR check)."""
    rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    results = face_mesh.process(rgb)
    if not results.multi_face_landmarks:
        return False
    
    # Simplified: check eye landmarks distance
    landmarks = results.multi_face_landmarks[0].landmark
    top = landmarks[159].y
    bottom = landmarks[145].y
    eye_open = abs(bottom - top)
    return eye_open < 0.012  # threshold for "closed"

def analyze_activity(frame):
    """Return activity classification."""
    if detect_phone_usage(frame):
        return "PHONE", "Employee is using mobile phone"
    if detect_drowsiness(frame):
        return "SLEEPING", "Employee appears drowsy/sleeping"
    return "WORKING", "Employee actively working"