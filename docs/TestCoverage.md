## Authentication:
### POST /api/v1/auth/login

| Scenario             | Priority | Automated | Status  |
|----------------------|----------|---------|---------|
| Valid login          | Critical | ✅ | DONE    |
| Invalid username     | Critical | ✅ | DONE |
| Invalid password     | Critical | ✅ | DONE |
| Blank username       | High | ✅ | DONE |
| Blank password       | High | ✅ | DONE |
| Username null        | High | ✅ | DONE |
| Password null        | High | ✅ | DONE |
| Empty request body   | High | ✅ | DONE |
| Malformed JSON       | Medium | ✅ | DONE |
| Missing Content-Type | Medium | ✅ | DONE |
| Wrong Content-Type   | Medium | ✅ | DONE |

## Accounts:

| Endpoint       | Scenario         | Priority | Automated | Status  |
| -------------- | ---------------- | -------- | --------- | ------- |
| POST /accounts | Valid account    | Critical | ✅         | ✅       |
| POST /accounts | Duplicate email  | Critical | ✅         | ✅       |
| POST /accounts | Invalid email    | High     | ✅         | ✅       |
| POST /accounts | Empty owner      | High     | ✅         | ✅       |
| POST /accounts | Null owner       | High     | ✅         | ✅       |
| POST /accounts | Blank owner      | High     | ❌         | Planned |
| POST /accounts | Too short owner  | High     | ✅         | ✅       |
| POST /accounts | Too long owner   | High     | ✅         | ✅       |
| POST /accounts | Negative balance | High     | ✅         | ✅       |
| POST /accounts | Missing JWT      | Critical | ❌         | Planned |
| POST /accounts | Invalid JWT      | Critical | ❌         | Planned |
| POST /accounts | Expired JWT      | Critical | ❌         | Planned |
