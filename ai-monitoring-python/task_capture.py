# task_capture.py
import psutil
import subprocess
import os

# ── Apps we want to capture and transfer ──────────────────────────────────────
TRACKABLE_APPS = {
    "notepad.exe": "notepad",
    "chrome.exe":  "chrome",
    "cmd.exe":     "cmd",
    "Code.exe":    "vscode",
    "WINWORD.EXE": "word",
    "EXCEL.EXE":   "excel",
    "POWERPNT.EXE":"powerpoint",
    "firefox.exe": "firefox",
    "msedge.exe":  "edge",
}

# ── 1. Get all running tracked apps ───────────────────────────────────────────
def get_running_tracked_apps():
    """Returns list of trackable apps currently running on this machine."""
    running = []
    seen_types = set()  # avoid duplicates (e.g. multiple chrome.exe processes)

    for proc in psutil.process_iter(['name', 'pid', 'exe']):
        try:
            name = proc.info['name']
            if name in TRACKABLE_APPS:
                app_type = TRACKABLE_APPS[name]
                if app_type not in seen_types:
                    running.append({
                        "processName": name,
                        "appType":     app_type,
                        "pid":         proc.info['pid']
                    })
                    seen_types.add(app_type)
        except (psutil.NoSuchProcess, psutil.AccessDenied):
            pass

    return running


# ── 2. Notepad content capture ─────────────────────────────────────────────────
def get_notepad_content():
    """Captures text currently open in Notepad via Win32 API."""
    try:
        import win32gui
        import win32con
        import win32api

        result = []

        def callback(hwnd, _):
            if win32gui.IsWindowVisible(hwnd):
                title = win32gui.GetWindowText(hwnd)
                if "notepad" in title.lower():
                    child = win32gui.FindWindowEx(hwnd, 0, "Edit", None)
                    if child:
                        length = win32api.SendMessage(child, win32con.WM_GETTEXTLENGTH)
                        buffer = win32api.SendMessage(
                            child, win32con.WM_GETTEXT,
                            length + 1, " " * (length + 1)
                        )
                        result.append(str(buffer))

        win32gui.EnumWindows(callback, None)
        return result[0] if result else ""

    except ImportError:
        print("   ⚠️  pywin32 not installed — cannot capture Notepad content")
        return ""
    except Exception as e:
        print(f"   ⚠️  Notepad capture failed: {e}")
        return ""


# ── 3. Chrome URL capture ──────────────────────────────────────────────────────
def get_active_chrome_url():
    """Gets the currently focused Chrome tab title (used as URL hint)."""
    try:
        result = subprocess.run(
            ['powershell', '-command',
             "(Get-Process chrome | Where-Object {$_.MainWindowTitle -ne ''}).MainWindowTitle"
             " | Select-Object -First 1"],
            capture_output=True, text=True, timeout=5
        )
        title = result.stdout.strip()
        # Chrome window title format: "Page Title - Google Chrome"
        if " - Google Chrome" in title:
            title = title.replace(" - Google Chrome", "").strip()
        return title
    except Exception as e:
        print(f"   ⚠️  Chrome URL capture failed: {e}")
        return ""


# ── 4. Firefox URL capture ─────────────────────────────────────────────────────
def get_active_firefox_url():
    """Gets the currently focused Firefox tab title."""
    try:
        result = subprocess.run(
            ['powershell', '-command',
             "(Get-Process firefox | Where-Object {$_.MainWindowTitle -ne ''}).MainWindowTitle"
             " | Select-Object -First 1"],
            capture_output=True, text=True, timeout=5
        )
        title = result.stdout.strip()
        if " — Mozilla Firefox" in title:
            title = title.replace(" — Mozilla Firefox", "").strip()
        return title
    except Exception as e:
        print(f"   ⚠️  Firefox URL capture failed: {e}")
        return ""


# ── 5. Edge URL capture ────────────────────────────────────────────────────────
def get_active_edge_url():
    """Gets the currently focused Edge tab title."""
    try:
        result = subprocess.run(
            ['powershell', '-command',
             "(Get-Process msedge | Where-Object {$_.MainWindowTitle -ne ''}).MainWindowTitle"
             " | Select-Object -First 1"],
            capture_output=True, text=True, timeout=5
        )
        title = result.stdout.strip()
        if " - Microsoft Edge" in title:
            title = title.replace(" - Microsoft Edge", "").strip()
        return title
    except Exception as e:
        print(f"   ⚠️  Edge URL capture failed: {e}")
        return ""


# ── 6. VS Code file capture ────────────────────────────────────────────────────
def get_active_vscode_file():
    """Gets the file/folder currently open in VS Code."""
    try:
        result = subprocess.run(
            ['powershell', '-command',
             "(Get-Process Code | Where-Object {$_.MainWindowTitle -ne ''}).MainWindowTitle"
             " | Select-Object -First 1"],
            capture_output=True, text=True, timeout=5
        )
        title = result.stdout.strip()
        # VS Code title format: "filename — folder — Visual Studio Code"
        if " - Visual Studio Code" in title:
            title = title.replace(" - Visual Studio Code", "").strip()
        return title
    except Exception as e:
        print(f"   ⚠️  VS Code capture failed: {e}")
        return ""


# ── 7. Word file capture ───────────────────────────────────────────────────────
def get_active_word_file():
    """Gets the document currently open in Microsoft Word."""
    try:
        result = subprocess.run(
            ['powershell', '-command',
             "(Get-Process WINWORD | Where-Object {$_.MainWindowTitle -ne ''}).MainWindowTitle"
             " | Select-Object -First 1"],
            capture_output=True, text=True, timeout=5
        )
        title = result.stdout.strip()
        if " - Word" in title:
            title = title.replace(" - Word", "").strip()
        return title
    except Exception as e:
        print(f"   ⚠️  Word capture failed: {e}")
        return ""


# ── 8. Excel file capture ──────────────────────────────────────────────────────
def get_active_excel_file():
    """Gets the workbook currently open in Microsoft Excel."""
    try:
        result = subprocess.run(
            ['powershell', '-command',
             "(Get-Process EXCEL | Where-Object {$_.MainWindowTitle -ne ''}).MainWindowTitle"
             " | Select-Object -First 1"],
            capture_output=True, text=True, timeout=5
        )
        title = result.stdout.strip()
        if " - Excel" in title:
            title = title.replace(" - Excel", "").strip()
        return title
    except Exception as e:
        print(f"   ⚠️  Excel capture failed: {e}")
        return ""


# ── 9. PowerPoint file capture ─────────────────────────────────────────────────
def get_active_powerpoint_file():
    """Gets the presentation currently open in PowerPoint."""
    try:
        result = subprocess.run(
            ['powershell', '-command',
             "(Get-Process POWERPNT | Where-Object {$_.MainWindowTitle -ne ''}).MainWindowTitle"
             " | Select-Object -First 1"],
            capture_output=True, text=True, timeout=5
        )
        title = result.stdout.strip()
        if " - PowerPoint" in title:
            title = title.replace(" - PowerPoint", "").strip()
        return title
    except Exception as e:
        print(f"   ⚠️  PowerPoint capture failed: {e}")
        return ""


# ── 10. Main capture function ──────────────────────────────────────────────────
def capture_current_work():
    """
    Main function called by task_receiver.py via /current-tasks endpoint.
    Returns a list of tasks describing what is currently running.
    """
    apps  = get_running_tracked_apps()
    tasks = []

    os.makedirs("C:\\wms_tasks", exist_ok=True)

    for app in apps:
        app_type = app["appType"]
        task = {"appType": app_type, "command": "", "content": ""}

        # ── Notepad ────────────────────────────────────────────────
        if app_type == "notepad":
            content = get_notepad_content()
            temp_path = "C:\\wms_tasks\\transfer_note.txt"
            with open(temp_path, "w", encoding="utf-8") as f:
                f.write(content if content else "Transferred from overloaded node")
            task["command"] = f'notepad.exe "{temp_path}"'
            task["content"] = content

        # ── Chrome ─────────────────────────────────────────────────
        elif app_type == "chrome":
            url = get_active_chrome_url()
            task["command"] = f'start chrome.exe "{url}"' if url else "start chrome.exe"
            task["content"] = url

        # ── Firefox ────────────────────────────────────────────────
        elif app_type == "firefox":
            url = get_active_firefox_url()
            task["command"] = f'start firefox.exe "{url}"' if url else "start firefox.exe"
            task["content"] = url

        # ── Edge ───────────────────────────────────────────────────
        elif app_type == "edge":
            url = get_active_edge_url()
            task["command"] = f'start msedge.exe "{url}"' if url else "start msedge.exe"
            task["content"] = url

        # ── CMD ────────────────────────────────────────────────────
        elif app_type == "cmd":
            task["command"] = "start cmd.exe"
            task["content"] = "CMD transferred"

        # ── VS Code ────────────────────────────────────────────────
        elif app_type == "vscode":
            file = get_active_vscode_file()
            task["command"] = f'code "{file}"' if file else "code"
            task["content"] = file

        # ── Word ───────────────────────────────────────────────────
        elif app_type == "word":
            doc = get_active_word_file()
            task["command"] = f'start WINWORD.EXE "{doc}"' if doc else "start WINWORD.EXE"
            task["content"] = doc

        # ── Excel ──────────────────────────────────────────────────
        elif app_type == "excel":
            wb = get_active_excel_file()
            task["command"] = f'start EXCEL.EXE "{wb}"' if wb else "start EXCEL.EXE"
            task["content"] = wb

        # ── PowerPoint ─────────────────────────────────────────────
        elif app_type == "powerpoint":
            ppt = get_active_powerpoint_file()
            task["command"] = f'start POWERPNT.EXE "{ppt}"' if ppt else "start POWERPNT.EXE"
            task["content"] = ppt

        print(f"   📸 Captured: {app_type} — {task['content'] or '(no content)'}")
        tasks.append(task)

    return tasks


# ── Run standalone for testing ─────────────────────────────────────────────────
if __name__ == "__main__":
    print("🔍 Capturing current work...\n")
    results = capture_current_work()
    if results:
        for t in results:
            print(f"  [{t['appType']}]")
            print(f"    Command : {t['command']}")
            print(f"    Content : {t['content']}\n")
    else:
        print("  No tracked apps currently running.")