export default function apiFetch(uri: string, init?: RequestInit): Promise<any> {
  const host = window.location.host === "localhost:8080"
    ? "http://localhost:8081/"
    : window.location.href
  const url = host + uri
  return fetch(url, init)
    .then(response =>
      response.ok 
        ? response.json()
        : Promise.reject(`${response.status} ${response.statusText}`)
    )
}