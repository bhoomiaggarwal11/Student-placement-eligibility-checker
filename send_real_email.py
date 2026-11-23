import sys
import json
import os
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart

def send_mail(to_email, subject, body_text):
    config_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), "email_config.json")
    
    if not os.path.exists(config_path):
        print(f"[MAIL-ERROR] Configuration file '{config_path}' not found.")
        return False

    try:
        with open(config_path, "r", encoding="utf-8") as f:
            config = json.load(f)
    except Exception as e:
        print(f"[MAIL-ERROR] Failed to read email_config.json: {e}")
        return False

    sender_email = config.get("sender_email", "").strip()
    app_password = config.get("app_password", "").strip()
    smtp_server = config.get("smtp_server", "smtp.gmail.com")
    smtp_port = int(config.get("smtp_port", 587))

    # Check if credentials are still placeholder
    if "YOUR_EMAIL" in sender_email or "xxxx" in app_password or not sender_email or not app_password:
        print("[MAIL-NOTICE] Real email credentials not yet filled in email_config.json.")
        print("[MAIL-NOTICE] (Please enter your Gmail and 16-character App Password in email_config.json to deliver real inbox emails).")
        return False

    try:
        msg = MIMEMultipart()
        msg["From"] = f"Training & Placement Cell <{sender_email}>"
        msg["To"] = to_email
        msg["Subject"] = subject
        msg.attach(MIMEText(body_text, "plain", "utf-8"))

        print(f"[SMTP] Connecting to {smtp_server}:{smtp_port}...")
        server = smtplib.SMTP(smtp_server, smtp_port, timeout=15)
        server.ehlo()
        server.starttls()
        server.ehlo()
        server.login(sender_email, app_password)
        server.send_message(msg)
        server.quit()

        print(f"[SUCCESS] Real email successfully delivered to inbox: {to_email}")
        return True

    except smtplib.SMTPAuthenticationError:
        print("[MAIL-ERROR] Authentication Failed! Check your Gmail and 16-character App Password.")
        return False
    except Exception as e:
        print(f"[MAIL-ERROR] Failed to send email via SMTP: {e}")
        return False

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python send_real_email.py <to_email> <subject> [body_file_or_text]")
        sys.exit(1)

    target_email = sys.argv[1]
    mail_subject = sys.argv[2]

    # If body is passed as a file path or direct string
    if len(sys.argv) >= 4:
        content_arg = sys.argv[3]
        if os.path.exists(content_arg):
            with open(content_arg, "r", encoding="utf-8") as bf:
                mail_body = bf.read()
        else:
            mail_body = content_arg
    else:
        mail_body = "This is a notification from PlacementHub."

    success = send_mail(target_email, mail_subject, mail_body)
    sys.exit(0 if success else 1)
