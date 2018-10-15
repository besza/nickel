import apiFetch from "./ApiFetch"
import { Account, Id, MonthlyBalance, NewAccount, } from "../Models"

export function getAll(): Promise<Array<Account>> {
  return apiFetch("api/accounts")
}

export function create(newAccount: NewAccount): Promise<Account> {
  return apiFetch("api/accounts", {
    method: "POST",
    body: JSON.stringify(newAccount)
  })
}

export function getBalances(accountId: Id<Account>): Promise<MonthlyBalance[]> {
  return apiFetch(`api/accounts/${accountId}/balance`)
}
