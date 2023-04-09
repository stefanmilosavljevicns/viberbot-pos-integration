from flask import Flask,request,render_template

app = Flask(__name__,template_folder='templates')

@app.route('/paymentinfo',methods=['GET'])
def index():
    viberId = request.args.get('viberId')
    viberPath = "https://sputnik-it.rs"+"/"+request.args.get('viberPath')+"/external-paying"
    return render_template('index.html',viberId=viberId,viberPath=viberPath)

if __name__ == "__main__":    
    app.run(host="0.0.0.0", port=5000, debug=True)