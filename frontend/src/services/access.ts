export enum AccessType {
  ADMITTED = 0,
  REQUESTED = 1,
  REQUEST_REJECTED = 2,
  INVITED = 3,
  INVITE_REJECTED = 4,
  LEFT = 5,
  BLOCKED = 6,
  KICKED = 7,
  BANNED = 8,
  NONE = 9,
}

export const ACCESS_TYPE_ARRAY: string[] = [
  "admitted",
  "requested",
  "request_rejected",
  "invited",
  "invite_rejected",
  "left",
  "blocked",
  "kicked",
  "banned",
  "none",
];
