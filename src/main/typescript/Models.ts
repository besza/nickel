export type Id<T> = string
export type LocalDate = string
export type Money = string

export interface NewAccount {
  readonly name: string
}

export interface Account extends NewAccount {
  readonly id: Id<Account>
}

export interface NewTransaction {
  readonly from: Id<Account>,
  readonly to: Id<Account>,
  readonly on: string,
  readonly amount: string,
  readonly description: string
}

export interface Transaction extends NewTransaction {
  readonly id: Id<Transaction>,
  readonly createdAt: string
}

export interface MonthlyBalance {
  readonly month: string,
  readonly in: Money,
  readonly out: Money,
  readonly balance: Money,
  readonly accountBalances: string[][]
}
