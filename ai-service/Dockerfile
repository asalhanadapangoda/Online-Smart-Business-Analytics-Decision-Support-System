FROM python:3.10-slim

WORKDIR /app

# Copy requirements and pre-downloaded wheels
COPY requirements.txt .
COPY wheels ./wheels

# Install dependencies from the local wheels folder (with online fallback for missing dependencies)
RUN pip install --no-cache-dir --find-links=./wheels -r requirements.txt

# Copy application source code
COPY app ./app

EXPOSE 8000

# Start Uvicorn bound to 0.0.0.0
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
