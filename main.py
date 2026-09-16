from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.button import Button
from kivy.uix.label import Label
from kivy.clock import Clock
from plyer import filechooser
from http.server import HTTPServer, BaseHTTPRequestHandler
import threading, json

gguf_path = {"v": None}
vosk_path = {"v": None}

class Handler(BaseHTTPRequestHandler):
    def do_POST(self):
        if self.path != "/chat":
            self.send_response(404); self.end_headers(); return
        n = int(self.headers.get("Content-Length", 0))
        body = self.rfile.read(n).decode() if n else "{}"
        try: msg = json.loads(body).get("message", "")
        except: msg = ""
        # STUB reply — swap with llama.cpp + vosk later
        reply = f"[stub] model={gguf_path['v']} vosk={vosk_path['v']} you said: {msg}"
        data = json.dumps({"reply": reply}).encode()
        self.send_response(200)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(data)))
        self.end_headers()
        self.wfile.write(data)
    def log_message(self, *a): pass

class Root(BoxLayout):
    def __init__(self, **k):
        super().__init__(orientation="vertical", padding=20, spacing=15, **k)
        self.status = Label(text="Pick both files, then START SERVER", size_hint_y=0.3)
        self.add_widget(self.status)
        self.add_widget(Button(text="Pick .gguf model", on_release=self.pick_gguf))
        self.add_widget(Button(text="Pick Vosk folder", on_release=self.pick_vosk))
        self.add_widget(Button(text="START SERVER", on_release=self.start))
        self.server = None

    def pick_gguf(self, *_):
        filechooser.open_file(on_selection=self._got_gguf,
                              filters=[("GGUF model", "*.gguf")])
    def _got_gguf(self, sel):
        if sel: gguf_path["v"] = sel[0]; self.status.text = f"GGUF: {sel[0]}"
    def pick_vosk(self, *_):
        filechooser.choose_dir(on_selection=self._got_vosk)
    def _got_vosk(self, sel):
        if sel: vosk_path["v"] = sel[0]; self.status.text = f"VOSK: {sel[0]}"

    def start(self, *_):
        if self.server: self.status.text = "Server already running on 0.0.0.0:8080"; return
        if not gguf_path["v"] or not vosk_path["v"]:
            self.status.text = "Pick BOTH files first"; return
        threading.Thread(target=self._serve, daemon=True).start()
        self.status.text = "SERVER RUNNING on 0.0.0.0:8080"

    def _serve(self):
        self.server = HTTPServer(("0.0.0.0", 8080), Handler)
        self.server.serve_forever()

class JarvisServer(App):
    def build(self): return Root()

if __name__ == "__main__":
    JarvisServer().run()
