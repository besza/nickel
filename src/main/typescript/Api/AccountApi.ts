import apiFetch from "./ApiFetch"
import { Account, NewAccount } from "../Models"

export function getAll(): Promise<Array<Account>> {
  return apiFetch("api/accounts")
}

export function create(newAccount: NewAccount): Promise<Account> {
  return apiFetch("api/accounts", {
    method: "POST",
    body: JSON.stringify(newAccount)
  })
}