from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.button import Button
from kivy.uix.label import Label
from kivy.uix.textinput import TextInput
from flask import Flask, request, jsonify
import threading, os

flask_app = Flask(__name__)

@flask_app.route('/chat', methods=['POST'])
def chat():
    msg = request.json.get('message','')
    return jsonify({"reply": f"JARVIS 32-bit OFFLINE heard: {msg}"})

def run_flask():
    flask_app.run(host='0.0.0.0', port=8080)

class JarvisApp(App):
    def build(self):
        threading.Thread(target=run_flask, daemon=True).start()
        box = BoxLayout(orientation='vertical', padding=10, spacing=10)
        self.info = Label(text='JARVIS BRAIN ONLINE\nOffline Server: port 8080\nVosk model path:\n/sdcard/Jarvis/model')
        self.txt
