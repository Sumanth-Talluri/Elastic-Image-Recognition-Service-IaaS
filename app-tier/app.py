
import boto3
from image_classification import get_preciction
import os

# S3 Config
BUCKET_NAME='cc-p1-input-bucket'
s3 = boto3.client('s3',
                    aws_access_key_id="AKIATJMRL2GQ5XQZJFHJ",       #keys.ACCESS_KEY_ID,
                    aws_secret_access_key="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", #keys.ACCESS_SECRET_KEY,
                    # aws_session_token="" keys.AWS_SESSION_TOKEN
    )

# SQS Config
REQ_QUEUE_NAME = "cc-p1-request-queue"
RESP_QUEUE_NAME = "cc-p1-response-queue"
QueueUrl = "https://sqs.us-east-1.amazonaws.com/226327515553/cc-p1-request-queue"
client = boto3.resource('sqs', region_name='us-east-1',
                    aws_access_key_id="AKIATJMRL2GQ5XQZJFHJ", 
                    aws_secret_access_key="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"
        )

queue_pull = boto3.client('sqs', region_name='us-east-1',
                        aws_access_key_id="AKIATJMRL2GQ5XQZJFHJ", 
                    aws_secret_access_key="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"
                    )

# get queue
queue = client.get_queue_by_name(QueueName=REQ_QUEUE_NAME)


# connect to S3 and upload the file
def upload(filename):
    msg = ""
    s3.upload_file(
        Bucket = BUCKET_NAME,
        Filename=filename,
        Key = filename
    )
    msg = "Uploaded!"

    return msg if msg else "Upload Failed!!"

def sqs_push(messageBody, queueName):

    response = client.get_queue_by_name(QueueName=queueName).send_message(MessageBody=messageBody)
    print("\n\nMessage Sent", response)
    return response

def sqs_pull():
    
    # Receive message
    response = queue_pull.receive_message(
        QueueUrl=QueueUrl,
        AttributeNames=[
            'SentTimestamp'
        ],
        MaxNumberOfMessages=1,
        MessageAttributeNames=[
            'All'
        ],
        VisibilityTimeout=20,
        WaitTimeSeconds=20
    )

    check = response.get("Messages", [])
    if not check:
        return

    print("\n\nMessage Received", response)
    filename = response["Messages"][0]["Body"]

    sqs_delete(response["Messages"][0]["ReceiptHandle"])
    s3.download_file(
        Bucket=BUCKET_NAME, Key=filename, Filename="/tmp/"+filename
    )

    model_res = get_preciction("/tmp/"+filename)
    img_name = model_res[0]
    result = model_res[1]

    print("uploading to output bucket " + filename)
    s3.put_object(
        Bucket = "cc-p1-output-bucket",
        Body=f"{img_name}, {result}",
        Key = img_name + ".txt"
    )

    sqs_push(f"{img_name}, {result}", RESP_QUEUE_NAME)

    print("deleting file: " + filename)
    os.remove("/tmp/"+filename)

    return model_res


def sqs_delete(id):
    res = queue_pull.delete_message(
            QueueUrl=QueueUrl,
            ReceiptHandle=id
    )
    return res

while True:
    sqs_pull()
