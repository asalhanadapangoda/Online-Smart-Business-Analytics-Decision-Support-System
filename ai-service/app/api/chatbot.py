from fastapi import APIRouter, Header
from typing import Optional
from app.schemas.schemas import ChatRequest, ChatResponse
from app.services import chatbot_service

router = APIRouter()


@router.post("/query", response_model=ChatResponse)
def process_query(request: ChatRequest, authorization: Optional[str] = Header(None)):
    result = chatbot_service.process_message(
        message=request.message,
        session_id=request.session_id,
        branch_id=request.branch_id,
        auth_token=authorization
    )
    return ChatResponse(**result)
