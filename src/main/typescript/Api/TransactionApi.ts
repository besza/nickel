import apiFetch from "./ApiFetch"
import { Account, Id, NewTransaction, Transaction } from "../Models"

export function get(month?: string, accountId?: Id<Account>): Promise<Transaction[]> {
  const params =
    [ month == null ? undefined : `month=${month}`
    , accountId == null ? undefined : `account=${accountId}`
    ]
    .filter(x => x != null)
    .join("&")
  const uri = "api/transactions?" + params
  return apiFetch(uri)
}

export function update(id: Id<Transaction>, newTransaction: NewTransaction): Promise<void> {
  return apiFetch("api/transactions/" + id, {
    method: "PUT",
    body: JSON.stringify(newTransaction)
  })
}

export function del(id: Id<Transaction>): Promise<void> {
  return apiFetch("api/transactions/" + id, { method: "DELETE" })
}

export function getMonths(): Promise<string[]> {
  return apiFetch("api/transactions/months")
}
