# CTR & EvPM Analysis & Visualization App Documentation

## Overview

The CTR & EvPM Analysis & Visualization application provides endpoints to upload CSV data for views and actions, retrieve CTR (Click-Through Rate), EvPM (Events Per Minute) data within a specified date range and tag, and aggregate views data by mmDma or siteId. Additionally, it offers chart endpoints to visually represent the data.

## Endpoints

### 1. Upload Views from CSV
**Endpoint:** `POST /views`

**Description:** Uploads view data from a CSV file. Accessible via Swagger.

### 2. Upload Actions from CSV
**Endpoint:** `POST /actions`

**Description:** Uploads action data from a CSV file. Accessible via Swagger.

### 3. Get CTR within Date Range and Tag
**Endpoint:** `GET /ctr`

**Description:** Retrieves CTR data within a specified date range and tag. Accessible via Swagger.

### 4. Get CTR Chart
**Endpoint:** `GET /ctrChart`

**Description:** Retrieves CTR data and renders a bar chart for visualization. To view the chart, copy the link to your browser.
```html
http://51.20.133.145:8080/ctrChart?dateFrom=2021-07-20T20%3A00%3A00&dateTo=2021-07-22T20%3A00%3A00&interval=HOUR
```

### 5. Get EvPM within Date Range and Tag
**Endpoint:** `GET /evpm`

**Description:** Retrieves EvPM data within a specified date range and tag. Accessible via Swagger.

### 6. Get EvPM Chart
**Endpoint:** `GET /evpmChart`

**Description:** Retrieves EvPM data and renders a bar chart for visualization. To view the chart, copy the link to your browser.
```html
http://51.20.133.145:8080/evpmChart?dateFrom=2021-07-20T20%3A00%3A00&dateTo=2021-07-25T20%3A00%3A00&interval=DAY&tag=registration
```
### 7. Aggregate Views Count by mmDma
**Endpoint:** `GET /viewsCountByMmDma`

**Description:** Aggregates the number of views by mmDma within a specified date range. Accessible via Swagger.

### 8. Aggregate Views Count by SiteId
**Endpoint:** `GET /viewsCountBySiteId`

**Description:** Aggregates the number of views by siteId within a specified date range. Accessible via Swagger.

### 9. Get Ctr Aggregate By mmDma within Date Range and Tag
**Endpoint:** `GET /ctrByMmDma`

**Description:** Retrieves CTR aggregate data by mmDma within a specified date range and tag. Accessible via Swagger.

### 10. Get Ctr Aggregate By mmDma Chart
**Endpoint:** `GET /ctrByMmDmaChart`

**Description:** Retrieves CTR aggregate data by mmDma and renders a bar chart for visualization. To view the chart, copy the link to your browser.
```html
http://51.20.133.145:8080/ctrByMmDmaChart?dateFrom=2021-07-20T20%3A00%3A00&dateTo=2021-07-25T20%3A00%3A00&interval=DAY&tag=registration
```

### 11. Get Ctr Aggregate By SiteId within Date Range and Tag
**Endpoint:** `GET /ctrBySiteId`

**Description:** Retrieves CTR aggregate data by siteId within a specified date range and tag. Accessible via Swagger.

### 12. Get Ctr Aggregate By SiteId Chart
**Endpoint:** `GET /ctrBySiteIdChart`

**Description:** Retrieves CTR aggregate data by siteId and renders a bar chart for visualization. To view the chart, copy the link to your browser.
```html
http://51.20.133.145:8080/ctrBySiteIdChart?dateFrom=2021-07-20T20%3A00%3A00&dateTo=2021-07-22T21%3A00%3A00&tag=registration
```

## Request Parameters

- `dateFrom` (required): Start date for data retrieval (e.g., 2021-07-20T20:00:00).
- `dateTo` (required): End date for data retrieval (e.g., 2021-07-22T20:00:00).
- `interval` (required): Time interval for data aggregation.
- `tag` (optional): Tag for filtering data.

## Response

The responses include data streams and charts, depending on the endpoint.

### Data Streams
Data streams are provided in the response body as a JSON array.

### Charts
Charts are rendered in the "barChart" view and include a graph title, Y-axis title, and data for both X and Y axes.

**Note:** 
For data endpoints, use Swagger: 
```html
http://51.20.133.145:8080/
```
For chart endpoints, view the charts by copying the link to your browser.
