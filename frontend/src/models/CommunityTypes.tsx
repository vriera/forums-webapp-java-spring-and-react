import { User, Notification, UserPreview } from "./UserTypes";

export type Community = {
  id: number;
  name: string;
  description: string;
  moderator?: User;
  userCount?: number;
  notifications?: Notification;
};

export type CommunityPreview = {
  name: string;
  uri: string;
  id: number;
};

export type CommunityCard = {
  id: number;
  name: string;
  moderator: UserPreview;
  userCount: number;
  uri: string;
  description?: string;
};

export type CommunitySearchParams = {
  page: number;
  size: number;
  query?: string;
};
