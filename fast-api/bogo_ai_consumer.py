import logging
import json
import uuid
import time # 시뮬레이션을 위한 임포트

from kafka_consumer_producer import start_consumer, send_message

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

# Spring Boot에서 보낸 요청을 수신할 토픽 (FuneralRegiste 이벤트)
KAFKA_INPUT_TOPIC = "aivlebigproject" 

# AI 처리 완료 후 Spring Boot로 보낼 토픽 (KafkaProcessor.INPUT_FUNERAL_REQUEST_PROCESSED)
KAFKA_OUTPUT_TOPIC = "funeral-request-processed"

CONSUMER_GROUP_ID = "bogo-ai-group"
TEMPLATE_ID = 1 # 부고장

def process_bogo_request(message_data: dict):
    """
    부고장 생성 요청 메시지를 처리하는 함수.
    여기서 실제 AI 모델 호출 및 파일 생성 로직을 구현합니다.
    """
    original_request_id = message_data.get("id") # Spring Boot의 FuneralRegiste.id
    if not original_request_id:
        logger.error(f"Received message without 'id': {message_data}")
        return

    logger.info(f"Processing obituary request for original ID: {original_request_id}")

    generated_file_url = None
    try:
        # ----------------------------------------------------------------------
        # ✨ 여기부터 실제 AI 모델 처리 및 파일 저장 로직 ✨
        # ----------------------------------------------------------------------
        
        # 1. AI 모델을 사용하여 부고장 데이터 생성 (예: 이미지 생성, 텍스트 요약 등)
        # funeral_info = message_data # Spring Boot에서 보낸 FuneralRegiste 데이터
        # result_of_ai = your_bogo_ai_model.process(funeral_info) 
        
        # 시뮬레이션: 3초 동안 작업 수행
        time.sleep(3) 
        
        # 2. 생성된 문서를 파일로 저장 (예: PDF, PNG, JPG)
        # 실제 환경에서는 S3, GCS 또는 다른 파일 서버에 저장하고 해당 URL을 반환해야 합니다.
        # 여기서는 임시 URL을 가정합니다.
        filename = f"obituary_{original_request_id}_{uuid.uuid4()}.pdf"
        generated_file_url = f"http://your-file-server.com/documents/{filename}" 
        logger.info(f"Obituary for request ID {original_request_id} generated. File URL: {generated_file_url}")

        # ----------------------------------------------------------------------
        # ✨ 실제 AI 로직 끝 ✨
        # ----------------------------------------------------------------------

        # 3. Spring Boot로 결과 메시지 발행
        output_payload = {
            "id": original_request_id,
            "template_id": TEMPLATE_ID,
            "file_url": generated_file_url,
            "message": "부고장 생성이 성공적으로 완료되었습니다."
        }
        send_message(
            topic=KAFKA_OUTPUT_TOPIC,
            key=str(original_request_id),
            value=output_payload
        )
        logger.info(f"Sent completion message for obituary (ID: {original_request_id}) to Spring Boot.")

    except Exception as e:
        logger.error(f"Error during obituary generation for ID {original_request_id}: {e}", exc_info=True)
        # 오류 발생 시 Spring Boot로 실패 메시지 발행
        error_output_payload = {
            "id": original_request_id,
            "template_id": TEMPLATE_ID,
            "file_url": None, # file_url을 None으로 보내서 실패를 알림
            "error": str(e),
            "message": "부고장 생성에 실패했습니다."
        }
        send_message(
            topic=KAFKA_OUTPUT_TOPIC,
            key=str(original_request_id),
            value=error_output_payload
        )
        logger.error(f"Sent error message for obituary (ID: {original_request_id}) to Spring Boot.")

def start_bogo_consumer():
    """부고장 AI Consumer를 시작하는 헬퍼 함수."""
    start_consumer(KAFKA_INPUT_TOPIC, CONSUMER_GROUP_ID, process_bogo_request)

# 이 파일이 단독으로 실행될 때 (테스트용)
if __name__ == "__main__":
    logger.info("Starting Bogo AI Consumer directly...")
    start_bogo_consumer()