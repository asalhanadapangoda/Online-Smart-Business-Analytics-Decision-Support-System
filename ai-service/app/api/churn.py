from fastapi import APIRouter
from app.schemas.schemas import ChurnRequest, ChurnResponse
from app.services.churn_service import churn_service_instance

router = APIRouter()


@router.post("/predict", response_model=ChurnResponse)
def predict_churn(request: ChurnRequest):
    """
    Customer churn prediction using a trained sklearn LogisticRegression model.
    Phase 2 implementation complete.
    """
    result = churn_service_instance.predict_customer_churn(
        customer_id=request.customer_id,
        days_since_last_purchase=request.days_since_last_purchase,
        total_purchases=request.total_purchases,
        average_purchase_value=request.average_purchase_value
    )
    
    return ChurnResponse(**result)
