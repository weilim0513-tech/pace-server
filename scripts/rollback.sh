#!/bin/bash

# 롤백할 버전 (commit SHA 또는 latest)
TARGET_VERSION=${1:-"latest"}
DOCKER_IMAGE="${DOCKER_USERNAME}/pace-wake"

echo "🔄 Rolling back to version: $TARGET_VERSION"

# 현재 버전 백업
CURRENT_VERSION=$(docker inspect pace-app --format='{{.Config.Image}}' 2>/dev/null | cut -d: -f2)
echo "📋 Current version: $CURRENT_VERSION"

# 새 이미지 풀
docker pull $DOCKER_IMAGE:$TARGET_VERSION

# 기존 컨테이너 중지 및 제거
docker stop pace-app
docker rm pace-app

# 롤백 버전으로 실행
docker run -d \
  --name pace-app \
  --restart unless-stopped \
  --network pace-network \
  -p 8080:8080 \
  --env-file /home/ec2-user/app/.env \
  $DOCKER_IMAGE:$TARGET_VERSION

echo "✅ Rollback completed to version: $TARGET_VERSION"
