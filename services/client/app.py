from flask import Flask,request,render_template, abort,url_for, redirect

app = Flask(__name__,template_folder='templates')

@app.route('/paymentinfo', methods=['POST'])
def index():
    referer = request.headers.get('referer')
    if referer != 'https://entegrasyon.asseco-see.com.tr/':
        abort(403)
    
    # process payment here
    # ...
    
    viberId = request.args.get('viberId')
    viberPath = "https://sputnik-it.rs"+request.args.get('viberPath')+"/external-paying?viberId="+viberId
    
    # redirect to a new URL to prevent refresh
    return redirect(url_for('payment_success', viberPath=viberPath))

@app.route('/payment_success', methods=['GET'])
def payment_success():
    referer = request.headers.get('referer')
    if referer != 'https://entegrasyon.asseco-see.com.tr/':
        abort(403)
    
    viberPath = request.args.get('viberPath')
    return render_template('success.html', viberPath=viberPath)


if __name__ == "__main__":    
    app.run(host="0.0.0.0", port=5000, debug=True)