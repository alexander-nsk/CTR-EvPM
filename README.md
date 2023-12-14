# CTR-EvPM
CTR &amp; EvPM Analysis &amp; Visualisation

This document provides detailed information about the Statistics API, which facilitates the interaction with statistical data related to views and actions.

## 1. Upload Views Data from CSV

- **Endpoint:** `/views`
- **Method:** `POST`
- **Consumes:** `multipart/form-data`
- **Produces:** `text/plain`
- **Summary:** Uploads view data from a CSV file.
- **Parameters:**
   - `file` (MultipartFile): CSV file containing view data.
- **Response:**
   - **Status:** 200 OK
   - **Content:** "Views uploaded: {number}"

## 2. Upload Actions Data from CSV

- **Endpoint:** `/actions`
- **Method:** `POST`
- **Consumes:** `multipart/form-data`
- **Produces:** `text/plain`
- **Summary:** Uploads action data from a CSV file.
- **Parameters:**
   - `file` (MultipartFile): CSV file containing action data.
- **Response:**
   - **Status:** 200 OK
   - **Content:** "Actions uploaded: {number}"

## 3. Load Test Data

- **Endpoint:** `/loadTestData`
- **Method:** `POST`
- **Produces:** `text/plain`
- **Summary:** Loads test data from the "resources/testdata" directory.
- **Response:**
   - **Status:** 200 OK
   - **Content:** "Test data loaded successfully"

## 4. Calculate Number of Views for Given mmDma and Dates

- **Endpoint:** `/views/allByMmDma`
- **Method:** `GET`
- **Produces:** `application/json`
- **Summary:** Calculates the number of views for a given mmDma and date range.
- **Parameters:**
   - `dateFrom` (LocalDate, required): Start date.
   - `dateTo` (LocalDate, required): End date.
   - `mmDma` (int, required): mmDma value.
- **Response:**
   - **Status:** 200 OK
   - **Content:** List of integers representing the number of views.

## 5. Calculate Number of Views for Given SiteId and Dates

- **Endpoint:** `/views/allBySiteId`
- **Method:** `GET`
- **Produces:** `application/json`
- **Summary:** Calculates the number of views for a given SiteId and date range.
- **Parameters:**
   - `dateFrom` (LocalDate, required): Start date.
   - `dateTo` (LocalDate, required): End date.
   - `siteId` (String, required): SiteId value.
- **Response:**
   - **Status:** 200 OK
   - **Content:** List of integers representing the number of views.

## 6. CTR for MmDma

- **Endpoint:** `/views/ctrByMmDma`
- **Method:** `GET`
- **Produces:** `application/json`
- **Summary:** Calculates Click-Through Rate (CTR) for MmDma.
- **Response:**
   - **Status:** 200 OK
   - **Content:** List of MmDmaCTR objects representing mmDma and CTR pairs.

## 7. CTR for MmDma with Tag

- **Endpoint:** `/views/ctrByMmDmaByTag`
- **Method:** `GET`
- **Produces:** `application/json`
- **Summary:** Calculates CTR for MmDma with a specific tag.
- **Parameters:**
   - `tag` (String): Tag for filtering.
- **Response:**
   - **Status:** 200 OK
   - **Content:** List of MmDmaCTR objects representing mmDma and CTR pairs with the specified tag.

## 8. CTR for SiteId

- **Endpoint:** `/views/ctrBySiteId`
- **Method:** `GET`
- **Produces:** `application/json`
- **Summary:** Calculates Click-Through Rate (CTR) for SiteId.
- **Response:**
   - **Status:** 200 OK
   - **Content:** List of SiteIdCTR objects representing siteId and CTR pairs.

## 9. CTR for SiteId with Tag

- **Endpoint:** `/views/ctrBySiteIdByTag`
- **Method:** `GET`
- **Produces:** `application/json`
- **Summary:** Calculates CTR for SiteId with a specific tag.
- **Parameters:**
   - `tag` (String): Tag for filtering.
- **Response:**
   - **Status:** 200 OK
   - **Content:** List of SiteIdCTR objects representing siteId and CTR pairs with the specified tag.

## 10. Clear Database Tables

- **Endpoint:** `/clearTables`
- **Method:** `GET`
- **Produces:** `text/plain`
- **Summary:** Clears database tables containing statistical data.
- **Response:**
   - **Status:** 200 OK
   - **Content:** "Database tables cleared successfully"


# Statistics Controller API Usage Examples

This section provides examples demonstrating the usage of the Statistics Controller API.

## 1. Upload Views Data from CSV

### cURL Example
```bash
curl -X POST -H "Content-Type: multipart/form-data" -F "file=@views_data.csv" http://localhost:8080/views