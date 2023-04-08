from flask import Flask

app = Flask(__name__)

@app.route('/paymentinfo')
def index():
    return 'Hello, World!'