from flask import Flask,request,render_template

app = Flask(__name__,template_folder='templates')

@app.route('/paymentinfo',methods=['GET'])
def index():
    viberID = request.args.get('viberID')
    viberPath = request.args.get('viberPath')
    return render_template('index.html')

if __name__ == "__main__":    
    app.run(host="0.0.0.0", port=5000, debug=True)