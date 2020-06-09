# FsmStateManagement
Finite state machine implementation with state management in microservices.

# Redis
Docker compose yaml is uploaded for local redis implementation.
docker-compose up command in the FsmStateManagement folder starts the redis.

- Finite State Machine is implemented on server with core package.
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

# Endpoints
You can see in the postman scripts as well.
- POST http://localhost:8088/rest/v1/fsm/create  : Creates FSM with states and events
- GET http://localhost:8088/rest/v1/fsm/{{transactionId}}/current/state  : Gets current state of FSM by transactionId
- GET http://localhost:8088/rest/v1/fsm/{{transactionId}}/next/{{eventName}}  : Moves Next State by given eventName
- DELETE http://localhost:8088/rest/v1/fsm/{{transactionId}}  : Removes all data(in memory, database, redis) of FSM
- GET http://localhost:8088/rest/v1/fsm/current/state/active  : Gets all current states of active FSMs
- GET http://localhost:8088/rest/v1/fsm/current/state/idle  : Gets all current states of idle FSMs
- PUT http://localhost:8088/rest/v1/fsm/{{transactionId}}/state/fail  : Sets failure of FSM
- PUT http://localhost:8088/rest/v1/fsm/{{transactionId}}/current/data : Set revertEndpoint,data,httpMethod dynamically

# Features
- Fsm's states are stored in memory and redis according to transaction identifier.
- Fsm's states can be idle and 5 minutes idle means to remove FSM from in memory.
- Automatically idle FSM can be restored.
- Revert logic : microservice is notified by rest request according to given state's data,httpMethod,revertEndpoint.
- Each rest request to microservices are stored in database and retries 3 times for fail cases. There is no loss in rest request data.
- If you switch auto.ddl create-drop to update, each transaction is persisted and retry logic also persisted as well. For further modification you can use any other database.

# For Other Implementations
- Rabbitmq implementation will be added.
- Extra api will be added for FSM

## SEE ALSO
https://github.com/grkn/FsmStateManagementClient

