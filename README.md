# FsmStateManagement
Finite state machine implementation with state management in microservices.

# Redis
Docker compose yaml is uploaded for local redis implementation.
docker-compose up command in the FsmStateManagement folder starts the redis.

- Finite State Machine is implemnted on server with core package.
- Also there is state management api.
- There are postman collections.

- http://localhost:8088/rest/v1/fsm/create
- Example body is below for POST 


```[
    {
        "stateName": "s1",
        "httpMethod": "GET",
        "revertEndpoint": "/api/v1/revert",
        "data": {
            "name": "customName",
            "id": "1"
        },
        "events": [
            {
                "eventName": "event1",
                "nextStateName": "s2"
            },
            {
            	"eventName": "event3",
            	"nextStateName": "s3"
            }
        ]
    },
    {
        "stateName": "s2",
        "httpMethod": "GET",
        "revertEndpoint": "/api/v1/revert",
        "data": {
            "name": "customName",
            "id": "2"
        },
        "events": [
        	{
                "eventName": "event2",
                "nextStateName": "s1"
            }	
        ]
    },
    {
        "stateName": "s3",
        "httpMethod": "GET",
        "revertEndpoint": "/api/v1/revert",
        "data": {
            "name": "customName",
            "id": "3"
        },
        "events" : [
            {
            	"eventName": "event4",
            	"nextStateName": "s1"
            }
        ]
    }
]
```
- State has fields with revertEndpoint,httpMethod,data to be called by FSM if a state fails.
- Also there are stateName, and events. Fsm implementation enables recursive states by design.
- For Example: state1 -> event1 -> state2 -> event2 -> state1. 

# For Other Implementations
- Rabbitmq implementation will be added.
- Extra api will be added for FSM



