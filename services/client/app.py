from flask import Flask

app = Flask(__name__)

@app.route('/paymentinfo',methods=['POST'])
def index():
    viberID = request.args.get('viberID')
    viberPath = request.args.get('viberPath')
    return "Hello, World! {viberID} and {viberPath}"

if __name__ == "__main__":    
    app.run(host="0.0.0.0", port=5000, debug=True)