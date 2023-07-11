import { User, UserPreview } from "./UserTypes";
import { Community, CommunityPreview } from "./CommunityTypes";
import { Answer } from "./AnswerTypes";

export type Question = {
  id: number;
  title: string;
  body: string;
  owner: User;
  image?: string;
  time: string;
  community: string;
  votes: number;
  userVote?:boolean;
  answers?: Answer[];
};

export type QuestionResponse = {
  id: number;
  title: string;
  body: string;
  owner: string;
  image?: string;
  time: string;
  community: string;
  votes: number;
  answers?: Answer[];
};

export type QuestionVoteResponse = {
  url:string;
  vote?:boolean;
  question:string;
  user:string;
}

export type QuestionCard = {
  id: number;
  title: string;
  body: string;
  owner: UserPreview;
  community: CommunityPreview;
  votes: number;
  date?: string;
  timestamp: string;
};

export type Vote = {
  id: number;
  vote: boolean;
  owner: User;
};
