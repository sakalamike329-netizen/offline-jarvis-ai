from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.button import Button
from kivy.uix.label import Label
from kivy.uix.scrollview import ScrollView
from plyer import filechooser
from http.server import HTTPServer, BaseHTTPRequestHandler
import threading, json, time

state = {
    "gguf": None,
    "vosk": None,
    "brain_up": False,
    "wake_up": False,
}

# ---------- Port 8080 : BRAIN ----------
class BrainHandler(BaseHTTPRequestHandler):
    def do_POST(self):
        if self.path != "/chat":
            self.send_response(404); self.end_headers(); return
        n = int(self.headers.get("Content-Length", 0))
        body = self.rfile.read(n).decode() if n else "{}"
        try: msg = json.loads(body).get("message", "")
        except: msg = ""
        # STUB — swap with llama.cpp later
        reply = f"[brain] model={state['gguf']} | you said: {msg}"
        data = json.dumps({"reply": reply}).encode()
        self.send_response(200)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(data)))
        self.end_headers()
        self.wfile.write(data)
    def log_message(self, *a): pass

# ---------- Port 2700 : WAKE WORD ----------
class WakeHandler(BaseHTTPRequestHandler):
    def do_POST(self):
        if self.path != "/wake":
            self.send_response(404); self.end_headers(); return
        n = int(self.headers.get("Content-Length", 0))
        _ = self.rfile.read(n) if n else b""
        # STUB — real vosk wake detection later
        data = json.dumps({"wake": False, "status": "listening"}).encode()
        self.send_response(200)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(data)))
        self.end_headers()
        self.wfile.write(data)
    def log_message(self, *a): pass

def run_server(handler, port):
    srv = HTTPServer(("0.0.0.0", port), handler)
    srv.serve_forever()

class Root(BoxLayout):
    def __init__(self, **k):
        super().__init__(orientation="vertical", padding=20, spacing=15, **k)
        self.log = Label(
            text="STEP 1: Pick your AI model (.gguf)",
            size_hint_y=0.5, halign="left", valign="top"
        )
        self.log.bind(size=lambda *a: setattr(self.log, "text_size", self.log.size))
        sc = ScrollView()
        sc.add_widget(self.log)
        self.add_widget(sc)

        self.add_widget(Button(text="1. Select AI model (.gguf)", on_release=self.pick_gguf))
        self.add_widget(Button(text="2. Select Vosk folder", on_release=self.pick_vosk))
        self.add_widget(Button(text="3. START SERVER", on_release=self.start))

    def _print(self, line):
        self.log.text += "\n" + line

    def pick_gguf(self, *_):
        filechooser.open_file(on_selection=self._got_gguf,
                              filters=[("GGUF model", "*.gguf")])
    def _got_gguf(self, sel):
        if sel:
            state["gguf"] = sel[0]
            self._print(f"> AI model set: {sel[0]}")
            self._print("STEP 2: Pick Vosk folder")
    def pick_vosk(self, *_):
        filechooser.choose_dir(on_selection=self._got_vosk)
    def _got_vosk(self, sel):
        if sel:
            state["vosk"] = sel[0]
            self._print(f"> Vosk set: {sel[0]}")
            self._print("STEP 3: Tap START SERVER")

    def start(self, *_):
        if not state["gguf"] or not state["vosk"]:
            self._print("!! Pick BOTH files first"); return
        if state["brain_up"] and state["wake_up"]:
            self._print("!! Already running"); return

        if not state["brain_up"]:
            threading.Thread(target=run_server,
                             args=(BrainHandler, 8080), daemon=True).start()
            state["brain_up"] = True
            self._print("> BRAIN server up on 0.0.0.0:8080")

        if not state["wake_up"]:
            threading.Thread(target=run_server,
                             args=(WakeHandler, 2700), daemon=True).start()
            state["wake_up"] = True
            self._print("> WAKE server up on 0.0.0.0:2700")

        self._print(">> ALL SYSTEMS ONLINE <<")

class JarvisServer(App):
    def build(self): return Root()

if __name__ == "__main__":
    JarvisServer().run()
