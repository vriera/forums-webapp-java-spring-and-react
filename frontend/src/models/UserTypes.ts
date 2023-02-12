export type User = {
  id: number;
  username: string;
  email: string;
  notifications?: Notification;
  karma?: Karma;
};

export type UserPreview = {
  username: string;
  uri: string;
};

export type Karma = {
  karma: number;
};

export type Notification = {
  requests: number;
  invites: number;
  total: number;
};
