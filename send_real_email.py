import sys
import json
import os
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart

def load_smtp_config():
    base_dir = os.path.dirname(os.path.abspath(__file__))
    config_file = os.path.join(base_dir, "email_config.json")

    if not os.path.exists(config_file):
        return None

    try:
        with open(config_file, "r", encoding="utf-8") as f:
            return json.load(f)
    except Exception as err:
        print(f"Error reading email config: {err}")
        return None

def send_notification(recipient, subject, content):
    config = load_smtp_config()
    if not config:
        print("Notice: email_config.json not found. Email logged to local file only.")
        return False

    sender = config.get("sender_email", "").strip()
    password = config.get("app_password", "").strip()
    server_host = config.get("smtp_server", "smtp.gmail.com")
    server_port = int(config.get("smtp_port", 587))

    # Basic check for empty or unconfigured sender details
    if not sender or not password or len(password) < 8:
        print("Notice: SMTP credentials not set in email_config.json (local logging active).")
        return False

    try:
        message = MIMEMultipart()
        message["From"] = f"Placement Cell <{sender}>"
        message["To"] = recipient
        message["Subject"] = subject
        message.attach(MIMEText(content, "plain", "utf-8"))

        with smtplib.SMTP(server_host, server_port, timeout=12) as client:
            client.starttls()
            client.login(sender, password)
            client.send_message(message)

        print(f"Email sent successfully to: {recipient}")
        return True

    except smtplib.SMTPAuthenticationError:
        print("SMTP Auth Error: Please verify your sender email and application password.")
        return False
    except Exception as err:
        print(f"SMTP Transmission Failed: {err}")
        return False

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python send_real_email.py <recipient_email> <subject> [body_text_or_path]")
        sys.exit(1)

    target = sys.argv[1]
    topic = sys.argv[2]
    body = "Placement Cell Notification"

    if len(sys.argv) >= 4:
        arg_val = sys.argv[3]
        if os.path.exists(arg_val):
            with open(arg_val, "r", encoding="utf-8") as bf:
                body = bf.read()
        else:
            body = arg_val

    ok = send_notification(target, topic, body)
    sys.exit(0 if ok else 1)
