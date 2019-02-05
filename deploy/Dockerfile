FROM java:8-alpine

RUN apk add --no-cache curl jq

WORKDIR /app

COPY entrypoint.sh /app/run.sh

EXPOSE 9000

HEALTHCHECK --start-period=1m30s --interval=30s --timeout=3s CMD curl -f http://localhost:9000/slack/ || exit 1

CMD ["/app/run.sh"]
