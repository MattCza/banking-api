## Authentication
### POST /api/v1/auth/login

| Scenario             | Priority | Automated | Status |
|----------------------|----------|-----------|--------|
| Valid login          | Critical | ✅         | DONE   |
| Invalid username     | Critical | ✅         | DONE   |
| Invalid password     | Critical | ✅         | DONE   |
| Blank username       | High     | ✅         | DONE   |
| Blank password       | High     | ✅         | DONE   |
| Username null        | High     | ✅         | DONE   |
| Password null        | High     | ✅         | DONE   |
| Empty request body   | High     | ✅         | DONE   |
| Malformed JSON       | Medium   | ✅         | DONE   |
| Missing Content-Type | Medium   | ✅         | DONE   |
| Wrong Content-Type   | Medium   | ✅         | DONE   |

## Accounts
### POST /api/v1/accounts

| Scenario                            | Priority | Automated | Status |
|-------------------------------------|----------|-----------|--------|
| Valid account                       | Critical | ✅         | DONE   |
| Duplicate email                     | Critical | ✅         | DONE   |
| Duplicate email - parallel requests | Critical | ✅         | DONE   |
| Empty owner name                    | High     | ✅         | DONE   |
| Null owner name                     | High     | ✅         | DONE   |
| Too short owner name                | High     | ✅         | DONE   |
| Too long owner name                 | High     | ✅         | DONE   |
| Empty email                         | High     | ✅         | DONE   |
| Null email                          | High     | ✅         | DONE   |
| Too long email                      | High     | ✅         | DONE   |
| Null initial balance                | High     | ✅         | DONE   |
| Negative initial balance            | High     | ✅         | DONE   |
| Too many integer digits in balance  | High     | ✅         | DONE   |
| Too many decimal places in balance  | High     | ✅         | DONE   |
| Missing JWT                         | Critical | ✅         | DONE   |
| Invalid JWT                         | Critical | ✅         | DONE   |
| Expired JWT                         | Critical | ✅         | DONE   |

### GET /api/v1/accounts/{id}

| Scenario             | Priority | Automated | Status |
|----------------------|----------|-----------|--------|
| Existing account     | Critical | ✅         | DONE   |
| Non-existing account | High     | ✅         | DONE   |
| Non-numeric ID       | High     | ✅         | DONE   |
| Missing JWT          | Critical | ✅         | DONE   |
| Invalid JWT          | Critical | ✅         | DONE   |
| Expired JWT          | Critical | ✅         | DONE   |

### DELETE /api/v1/accounts/{id}

| Scenario                                      | Priority | Automated | Status |
|-----------------------------------------------|----------|-----------|--------|
| Delete existing account                       | Critical | ✅         | DONE   |
| Verify deleted account is no longer available | Critical | ✅         | DONE   |
| Delete non-existing account                   | High     | ✅         | DONE   |
| Delete Non-numeric ID                         | Medium   | ✅         | DONE   |
| Missing JWT                                   | Critical | ✅         | DONE   |
| Invalid JWT                                   | Critical | ✅         | DONE   |
| Expired JWT                                   | Critical | ✅         | DONE   |
