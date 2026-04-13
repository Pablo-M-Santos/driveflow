# k6 - Teste de Concorrência de Aluguel

Script para validar concorrência no `POST /api/v1/rentals`.

## O que ele testa

- múltiplas requisições simultâneas para o mesmo veículo
- conflito de período de aluguel
- resposta esperada: `201` para o primeiro aluguel e `409` para as tentativas seguintes

## Como executar

```bash
cd /home/pablo-santos/Documentos/projetos-pessoais/DriveFlow/backend
k6 run k6/rental-concurrency.js
```

## Execucao recomendada (incremental)

```bash
VUS=1 DURATION=10s k6 run k6/rental-concurrency.js
VUS=2 DURATION=10s k6 run k6/rental-concurrency.js
VUS=5 DURATION=15s k6 run k6/rental-concurrency.js
VUS=10 DURATION=20s k6 run k6/rental-concurrency.js
```

## Parâmetros opcionais

```bash
BASE_URL=http://localhost:8080/api/v1 \
CUSTOMER_ID=4 \
VEHICLE_ID=6 \
START_DATE=2026-05-20 \
END_DATE=2026-05-22 \
VUS=10 \
DURATION=5s \
k6 run k6/rental-concurrency.js
```

## Observação importante

Use um `vehicleId` que esteja livre para o período escolhido. Assim você consegue observar um cenário com pelo menos um `201` e as demais respostas como `409`, comprovando a proteção contra concorrência.

No resumo do k6, acompanhe os contadores customizados:

- `status_201`: reservas criadas com sucesso
- `status_409`: conflitos bloqueados (esperado em corrida)
- `status_400`: validacao de entrada/regra
- `status_other`: resposta inesperada (investigar logs)

