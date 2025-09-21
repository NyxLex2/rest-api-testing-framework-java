# 🐛 API Bugs & Framework Recommendations

This document categorizes identified issues into **actual API bugs** that need fixing and **framework improvements** that would enhance the testing framework.


These are actual bugs in the API that violate business rules, security, or REST principles.

## BUG-001: Player Creation Returns Null Fields
**Severity:**  **CRITICAL**  
**Impact:** Data integrity validation impossible

**Issue:** API returns HTTP 200 but most response fields are `null` except `id` and `login`.

**Expected:** Complete player object with all fields populated  
**Actual:** `{"id": 123, "age": null, "genderEnum": null, "login": "user", "password": null, "roleEnum": null, "screenName": null}`

---

## BUG-002: API Rejects Valid Minimum Age
**Severity:**  **CRITICAL**  
**Impact:** Business rule violation

**Issue:** API rejects `age=16` which should be minimum valid age.

**Expected:** `GET /player/create/supervisor?age=16&...` → HTTP 200  
**Actual:** `GET /player/create/supervisor?age=16&...` → HTTP 400

---

## BUG-003: No Password Validation
**Severity:**  **CRITICAL**  
**Impact:** Security vulnerability

**Issue:** API accepts empty/invalid passwords during creation and updates.

**Expected:** `400 Bad Request` for invalid passwords  
**Actual:** `200 OK` with weak passwords accepted

---

## BUG-004: Wrong Status Codes for Non-Existent Resources
**Severity:**  **CRITICAL**  
**Impact:** Misleading API responses

**Issue:** API returns `200 OK` for operations on non-existent players.

**Expected:** `404 Not Found` for non-existent resources  
**Actual:** `200 OK` with empty/null responses

---

## BUG-005: Invalid Age Values Accepted in Updates
**Severity:**  **CRITICAL**  
**Impact:** Data integrity violation

**Issue:** Update endpoint accepts `age=0` which violates business rules.

**Expected:** `400 Bad Request` for invalid ages  
**Actual:** `200 OK` with invalid age persisted

---

## BUG-006: Circular Dependency Prevents Supervisor Creation
**Severity:**  **CRITICAL**  
**Impact:** Business logic flaw

**Issue:** No role can create supervisor players via API.

**Expected:** Some role should be able to create supervisors  
**Actual:** All roles return 400/403 when trying to create supervisors

---

## BUG-007: Unauthorized Player Deletion Allowed
**Severity:**  **CRITICAL**  
**Impact:** Security vulnerability

**Issue:** Users can delete other players without proper authorization.

**Expected:** `403 Forbidden` for unauthorized deletion  
**Actual:** `204 No Content` - deletion succeeds

---

#  **HIGH PRIORITY API BUGS** (Should Fix)

## BUG-008: Create/Update Responses Return Null Fields
**Severity:**  **HIGH**  
**Impact:** Response validation challenges

**Issue:** Create/update operations return null fields despite successful persistence.

**Expected:** Complete response data matching persisted state  
**Actual:** Null fields in responses, data correctly stored

---

## BUG-009: Empty PATCH Body Accepted
**Severity:**  **HIGH**  
**Impact:** Violates REST best practices

**Issue:** API accepts empty `{}` request bodies for PATCH operations.

**Expected:** `400 Bad Request` for empty PATCH bodies  
**Actual:** `200 OK` with no-op operation

---

#  **FRAMEWORK IMPROVEMENTS** (Recommendations)

These are not bugs but recommendations to improve the testing framework.

## REC-001: Non-RESTful HTTP Method Usage
**Type:** 🟡 **API Design Issue**  
**Priority:** Medium

**Issue:** API uses non-standard HTTP methods:
- `GET` for creation (should be `POST`)
- `POST` for retrieval (should be `GET`)

**Recommendation:** Framework already handles this with custom methods, but consider documenting this as API limitation.

---

## REC-002: Inconsistent Parameter Passing
**Type:**  **API Design Issue**  
**Priority:** Medium

**Issue:** API uses different parameter methods inconsistently:
- Query params for creation
- JSON body for retrieval
- JSON body for update/delete

**Recommendation:** Framework already handles this, but consider standardizing API design.

---

**Last Updated:** 2025-09-21
