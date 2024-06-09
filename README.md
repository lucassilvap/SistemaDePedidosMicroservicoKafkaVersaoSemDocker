 Baixe o kafka : https://downloads.apache.org/kafka/3.7.0/kafka_2.12-3.7.0.tgz.

 Siga para o caminho em que extraiu o kafka C:\kafka_2.13-3.7.0(esse é o meu caso).

 .\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties execute esse primeiro comando. 
 .\bin\windows\kafka-server-start.bat .\config\server.properties execute esse segundo comando.

Em nossa arquitetura, teremos 5 serviços:

* **orderservice**: microsserviço responsável apenas por gerar um pedido inicial, e receber uma notificação. Aqui que teremos endpoints REST para inciar o processo e recuperar os dados dos eventos. O banco de dados utilizado será o MongoDB.
* **orquestradorservice**: microsserviço responsável por orquestrar todo o fluxo de execução da Saga, ele que saberá qual microsserviço foi executado e em qual estado, e para qual será o próximo microsserviço a ser enviado, este microsserviço também irá salvar o processo dos eventos. Este serviço não possui banco de dados.
* **servicovalidacaodeproduto**: microsserviço responsável por validar se o produto informado no pedido existe e está válido. Este microsserviço guardará a validação de um produto para o ID de um pedido. O banco de dados utilizado será o H2.
* **servicodepagamento**: microsserviço responsável por realizar um pagamento com base nos valores unitários e quantidades informadas no pedido. Este microsserviço guardará a informação de pagamento de um pedido. O banco de dados utilizado será o H2.
* **servicodeinventario**: microsserviço responsável por realizar a baixa do estoque dos produtos de um pedido. Este microsserviço guardará a informação da baixa de um produto para o ID de um pedido. O banco de dados utilizado será o H2.

* ### Endpoint para iniciar a saga:

**POST** http://localhost:8090/api/order

Payload:

```json
{
  "orderProduct": [
    {
      "product": {
        "code": "COMIC_BOOKS",
        "unitValue": 15.50
      },
      "quantity": 3
    },
    {
      "product": {
        "code": "BOOKS",
        "unitValue": 9.90
      },
      "quantity": 1
    }
  ]
}
```

Resposta:

```json
{
  "id": "64429e987a8b646915b3735f",
  "orderProduct": [
    {
      "product": {
        "code": "COMIC_BOOKS",
        "unitValue": 15.5
      },
      "quantity": 3
    },
    {
      "product": {
        "code": "BOOKS",
        "unitValue": 9.9
      },
      "quantity": 1
    }
  ],
  "createdAt": "2023-04-21T14:32:56.335943085",
  "transactionId": "1682087576536_99d2ca6c-f074-41a6-92e0-21700148b519"
}
```

**GET** http://localhost:8090/api/event

Payload:
```json
{
"orderId":"665fae8a094ecc1ef7734980",
"transactionId":null
}
```

Resposta:

```json

{
    "id": "665fae8a094ecc1ef7734981",
    "transactionId": "1717546634045_5ca6f892-bbe3-4704-9fcb-469ab5d1176e",
    "orderId": "665fae8a094ecc1ef7734980",
    "payload": {
        "id": "665fae8a094ecc1ef7734980",
        "products": [
            {
                "product": {
                    "code": "COMIC_BOOKS",
                    "unitValue": "15.50"
                },
                "quantity": 3
            },
            {
                "product": {
                    "code": "BOOKS",
                    "unitValue": "9.90"
                },
                "quantity": 1
            }
        ],
        "localDateTime": "2024-06-04T21:17:14.045",
        "transactionId": "1717546634045_5ca6f892-bbe3-4704-9fcb-469ab5d1176e",
        "totalAmount": 0.0,
        "totalItems": 0
    },
    "source": null,
    "status": null,
    "eventHistory": null,
    "createdAt": "2024-06-04T21:17:14.095"
}
```
**GET**  http://localhost:8090/api/event/all

Resposta:

```json

[
    {
        "id": "665fae8a094ecc1ef7734981",
        "transactionId": "1717546634045_5ca6f892-bbe3-4704-9fcb-469ab5d1176e",
        "orderId": "665fae8a094ecc1ef7734980",
        "payload": {
            "id": "665fae8a094ecc1ef7734980",
            "products": [
                {
                    "product": {
                        "code": "COMIC_BOOKS",
                        "unitValue": "15.50"
                    },
                    "quantity": 3
                },
                {
                    "product": {
                        "code": "BOOKS",
                        "unitValue": "9.90"
                    },
                    "quantity": 1
                }
            ],
            "localDateTime": "2024-06-04T21:17:14.045",
            "transactionId": "1717546634045_5ca6f892-bbe3-4704-9fcb-469ab5d1176e",
            "totalAmount": 0.0,
            "totalItems": 0
        },
        "source": null,
        "status": null,
        "eventHistory": null,
        "createdAt": "2024-06-04T21:17:14.095"
    },
    {
        "id": "665f9afc1f3aa7625afe2a8f",
        "transactionId": "1717541628557_5df8d13a-8076-42db-bea6-0fc81a53e45e",
        "orderId": "665f9afc1f3aa7625afe2a8e",
        "payload": {
            "id": "665f9afc1f3aa7625afe2a8e",
            "products": [
                {
                    "product": {
                        "code": "COMIC_BOOKS",
                        "unitValue": "15.50"
                    },
                    "quantity": 3
                },
                {
                    "product": {
                        "code": "BOOKS",
                        "unitValue": "9.90"
                    },
                    "quantity": 1
                }
            ],
            "localDateTime": "2024-06-04T19:53:48.557",
            "transactionId": "1717541628557_5df8d13a-8076-42db-bea6-0fc81a53e45e",
            "totalAmount": 0.0,
            "totalItems": 0
        },
        "source": null,
        "status": null,
        "eventHistory": null,
        "createdAt": "2024-06-04T19:53:48.627"
    }
]
```
