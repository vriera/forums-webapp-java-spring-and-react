export enum AccessType {
  ADMITTED = 0,
  REQUESTED = 1,
  REQUEST_REJECTED = 2,
  INVITED = 3,
  INVITE_REJECTED = 4,
  LEFT = 5,
  BLOCKED_COMMUNITY = 6,
  KICKED = 7,
  BANNED = 8,
  NONE = 9,
}

export const ACCESS_TYPE_ARRAY: String[] = [
  "admitted",
  "requested",
  "request-rejected",
  "invited",
  "invite-rejected",
  "left",
  "blocked",
  "kicked",
  "banned",
  "none",
];

export const ACCESS_TYPE_ARRAY_ENUM: String[] = [
  "ADMITTED",
  "REQUESTED",
  "REQUEST_REJECTED",
  "INVITED",
  "INVITE_REJECTED",
  "LEFT",
  "BLOCKED_COMMUNITY",
  "KICKED",
  "BANNED",
  "NONE",
];
