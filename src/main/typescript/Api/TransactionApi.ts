import apiFetch from "./ApiFetch"
import { Account, Id, Transaction } from "../Models"

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

export function getMonths(): Promise<string[]> {
  return apiFetch("api/transactions/months")
}
