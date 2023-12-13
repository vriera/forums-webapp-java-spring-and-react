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

export type CommunityResponse = {
  id: number;
  name: string;
  moderator: string;
  userCount: number;
  uri: string;
  description?: string;
  questions: string;
};

export type CommunitySearchParams = {
  page: number;
  // size: number;
  query?: string;
};
