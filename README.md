# FsmStateManagement
Finite state machine implementation with state management in microservices.

# Docker compose yaml
Docker compose yaml is uploaded for local redis implementation.

- Finite State Machine is implemnted on server with core package.
- Also there is state management api.
- There are postman collections for examples.

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
- Also there are stateName, and events. Fsm implementation enables recursive FSM for design issues.
- For Example: state1 -> event1 -> state2 -> event2 -> state1. 


