from flask import Flask,request,render_template, abort, session
import secrets
app = Flask(__name__,template_folder='templates')
app.secret_key = secrets.token_hex(16)
@app.route('/paymentinfo',methods=['POST'])
def index():
    referer = request.headers.get('referer')
    if referer != 'https://entegrasyon.asseco-see.com.tr/':
        abort(403)  # Return a 403 Forbidden error if the referer is not the expected URL
    viberId = request.args.get('viberId')
    viberPath = "https://sputnik-it.rs"+request.args.get('viberPath')+"/external-paying?viberId="+viberId
        abort(403)  # Return a 403 Forbidden error if the referer is not the expected URL
    # Check if payment has already been processed
    if session.get('payment_processed'):
        # Redirect user to another page or show an error message
        return 'Payment has already been processed'
        return 'Link nije važeći, plaćanje je već izvršeno!'
    response_msg = request.form.get('responseMsg')
    if response_msg == 'Approved':
        viberId = request.args.get('viberId')
        viberPath = "https://sputnik-it.rs"+request.args.get('viberPath')+"/external-paying?viberId="+viberId
        session['payment_processed'] = True
        return render_template('sucess.html',viberPath=viberPath)
    else:
        viberId = request.args.get('viberId')
        viberPath = "https://sputnik-it.rs"+request.args.get('viberPath')+"/external-failure?viberId="+viberId
        session['payment_processed'] = True
        return render_template('fail.html',viberPath=viberPath)

    # Mark payment as processed in the session
    session['payment_processed'] = True

    return render_template('sucess.html',viberPath=viberPath)



if __name__ == "__main__":    
    app.run(host="0.0.0.0", port=5000, debug=True)