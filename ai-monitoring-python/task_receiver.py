# task_receiver.py
from flask import Flask, request, jsonify
import subprocess, threading, os

app = Flask(__name__)

@app.route('/execute', methods=['POST'])
def execute_task():
    data = request.json
    task_name    = data.get('taskName', '')
    task_payload = data.get('taskPayload', '')  # actual command to run
    
    print(f"📥 Received task: {task_name}")
    print(f"   Payload: {task_payload}")
    
    # Run the task in a background thread
    def run():
        try:
            result = subprocess.run(
                task_payload,
                shell=True,
                capture_output=True,
                text=True,
                timeout=60
            )
            print(f"✅ Task done: {result.stdout}")
        except Exception as e:
            print(f"❌ Task failed: {e}")
    
    threading.Thread(target=run).start()
    return jsonify({"status": "accepted", "task": task_name})

if __name__ == '__main__':
    app.run(port=9090)  # agent listens on port 9090