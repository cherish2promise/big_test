# samang_ai_consumer.py

import logging
import json
import uuid
import time

from kafka_consumer_producer import start_consumer, send_message

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

KAFKA_INPUT_TOPIC = "samang-ai-request" # Spring Boot의 outboundSamangAiRequest 에 해당
KAFKA_OUTPUT_TOPIC = "samang-ai-processed" # KafkaProcessor.INPUT_SAMANG_AI_PROCESSED 에 해당

CONSUMER_GROUP_ID = "samang-ai-group"
TEMPLATE_ID = 2 # 사망 신고서

def process_samang_request(message_data: dict):
    """사망 신고서 생성 요청 메시지를 처리하는 함수."""
    original_request_id = message_data.get("id")
    if not original_request_id:
        logger.error(f"Received message without 'id': {message_data}")
        return

    logger.info(f"Processing deceased report request for original ID: {original_request_id}")

    generated_file_url = None
    try:
        # ----------------------------------------------------------------------
        # ✨ 여기부터 실제 AI 모델 처리 및 파일 저장 로직 ✨
        # ----------------------------------------------------------------------
        time.sleep(2) # 시뮬레이션
        filename = f"deceased_report_{original_request_id}_{uuid.uuid4()}.pdf"
        generated_file_url = f"http://your-file-server.com/documents/{filename}" 
        logger.info(f"Deceased report for request ID {original_request_id} generated. File URL: {generated_file_url}")
        # ----------------------------------------------------------------------
        # ✨ 실제 AI 로직 끝 ✨
        # ----------------------------------------------------------------------

        output_payload = {
            "id": original_request_id,
            "template_id": TEMPLATE_ID,
            "file_url": generated_file_url,
            "message": "사망 신고서 생성이 성공적으로 완료되었습니다."
        }
        send_message(KAFKA_OUTPUT_TOPIC, str(original_request_id), output_payload)
        logger.info(f"Sent completion message for deceased report (ID: {original_request_id}) to Spring Boot.")

    except Exception as e:
        logger.error(f"Error during deceased report generation for ID {original_request_id}: {e}", exc_info=True)
        error_output_payload = {
            "id": original_request_id,
            "template_id": TEMPLATE_ID,
            "file_url": None,
            "error": str(e),
            "message": "사망 신고서 생성에 실패했습니다."
        }
        send_message(KAFKA_OUTPUT_TOPIC, str(original_request_id), error_output_payload)
        logger.error(f"Sent error message for deceased report (ID: {original_request_id}) to Spring Boot.")

def start_samang_consumer():
    """사망 신고서 AI Consumer를 시작하는 헬퍼 함수."""
    start_consumer(KAFKA_INPUT_TOPIC, CONSUMER_GROUP_ID, process_samang_request)

if __name__ == "__main__":
    logger.info("Starting Samang AI Consumer directly...")
    start_samang_consumer()