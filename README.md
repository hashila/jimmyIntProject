# Jimmy Routing Service

Spring Boot service that calculates a land-border route between two countries using their `cca3` codes.

## Project details

- Project naming updated from Emirates to Jimmy (`com.jimmy` package/group).
- API endpoint: `GET /routing/{origin}/{destination}`
- Data source: `https://raw.githubusercontent.com/mledoze/countries/master/countries.json`
- Route algorithm: Breadth-First Search (BFS) for efficient shortest path in an unweighted graph.

## Build and run

From `practice/`:

```powershell
./mvnw.cmd clean test
./mvnw.cmd spring-boot:run
```

The service starts on `http://localhost:8080` by default.

## Example request

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/routing/CZE/ITA"
```

Example response:

```json
{
  "route": ["CZE", "AUT", "ITA"]
}
```

If no land route exists, the API returns HTTP `400 Bad Request`.
