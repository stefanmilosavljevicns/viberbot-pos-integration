from flask import Flask,request,render_template, abort

app = Flask(__name__,template_folder='templates')

@app.route('/paymentinfo',methods=['POST'])
def index():
    referer = request.headers.get('referer')
    print("BODY:"+request.data.decode('utf-8'))
    app.logger.warning('BODY: %s', request.data.decode('utf-8'))
    app.logger.warning('Invalid referer: %s', referer)    
    if referer != 'https://entegrasyon.asseco-see.com.tr/chipcard/pay3d/':
        abort(403)  # Return a 403 Forbidden error if the referer is not the expected URL    
    viberId = request.args.get('viberId')
    viberPath = "https://sputnik-it.rs"+request.args.get('viberPath')+"/external-paying?viberId="+viberId
    return render_template('index.html',viberPath=viberPath)

if __name__ == "__main__":    
    app.run(host="0.0.0.0", port=5000, debug=True)