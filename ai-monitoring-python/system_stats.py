import psutil
import socket

def get_system_stats():
    cpu = psutil.cpu_percent(interval=1)
    mem = psutil.virtual_memory()
    return {
        "cpuUsage": cpu,
        "memoryUsage": mem.percent,
        "availableMemoryGB": round(mem.available / (1024**3), 2),
        "activeTasks": len(psutil.pids())
    }

def get_ip():
    try:
        return socket.gethostbyname(socket.gethostname())
    except:
        return "127.0.0.1"