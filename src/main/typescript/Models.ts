export type Id<T> = string
export type LocalDate = string
export type Money = string

export interface NewAccount {
  readonly name: string
}

export interface Account {
  readonly id: Id<Account>,
  readonly name: string
}

export interface Transaction {
  readonly id: Id<Transaction>,
  readonly createdAt: string,
  readonly from: Id<Account>,
  readonly to: Id<Account>,
  readonly on: LocalDate,
  readonly amount: Money,
  readonly description: String
}
