import { User } from "./UserTypes";
import { Community } from "./CommunityTypes";
import { Question } from "./QuestionTypes";

export type Answer = {
  id: number;
  title: string;
  body: string;
  owner: User;
  verify: boolean;
  question?: Question;
  userVote?: boolean;
  url: string;
  time: string;
  date: string;
  voteCount: number;
  votes?: number;
};

export type AnswerVoteResponse = {
  url:string;
  vote?:boolean;
  answer:string;
  user:string;
}

export type AnswerResponse = {
  id: number;
  title: string;
  body: string;
  owner: string;
  verify: boolean;
  question: string;
  community: string;
  userVote?: boolean;
  url: string;
  time: string;
  date: string;
  votes: string;
  voteCount:number;
};
