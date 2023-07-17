import {
  createAnswer,
  vote,
  deleteVote,
  getByOwner,
  unVerifyAnswer,
  verifyAnswer,
} from "../services/answers";
import { HTTPStatusCodes, NotFoundError } from "../models/HttpTypes";
import { api } from "../services/api";
import MockAdapter from "axios-mock-adapter";

describe("AnswersService", () => {
  let mockAxios = new MockAdapter(api);

  beforeEach(async () => {
    mockAxios.reset();
  });

  it("Should create answer when given a valid question ID", async () => {
    const answer = "This is a test answer";
    const idQuestion = 1;

    mockAxios.onPost(`/answers/${idQuestion}`).reply(HTTPStatusCodes.CREATED);
    let spy = jest.spyOn(api, "post");

    await createAnswer(answer, idQuestion);

    expect(spy).toHaveBeenCalledWith(`/answers/${idQuestion}`, {
      body: answer,
    });

    spy.mockRestore();
  });

  it("Should throw error when creating answer with invalid question ID", async () => {
    const answer = "answer";
    const idQuestion = -1;

    mockAxios.onPost(`/answers/${idQuestion}`).reply(HTTPStatusCodes.NOT_FOUND);
    
    await expect(createAnswer(answer, idQuestion)).rejects.toThrow(NotFoundError);
  });
  it("Should throw error when voting with invalid answer ID", async () => {
    const userId = 1;
    const id = -1;
    const voteValue = true;

    mockAxios
      .onPut(`/answers/${id}/votes/users/${userId}?vote=${voteValue}`)
      .reply(HTTPStatusCodes.NOT_FOUND);

    await expect(vote(userId, id, voteValue)).rejects.toThrow(NotFoundError);
  });

  it("Should throw error when deleting vote with invalid answer ID", async () => {
    const userId = 1;
    const id = -1;

    mockAxios
      .onDelete(`/answers/${id}/votes/users/${userId}`)
      .reply(HTTPStatusCodes.NOT_FOUND);

    await expect(deleteVote(userId, id)).rejects.toThrow(NotFoundError);
  });

  it("Should throw error when verifying answer with invalid answer ID", async () => {
    const id = -1;

    mockAxios.onPost(`/answers/${id}/verification/`).reply(HTTPStatusCodes.NOT_FOUND);

    await expect(verifyAnswer(id)).rejects.toThrow(NotFoundError);
  });

  it("Should throw error when unverifying answer with invalid answer ID", async () => {
    const id = -1;

    mockAxios
      .onDelete(`/answers/${id}/verification/`)
      .reply(HTTPStatusCodes.NOT_FOUND);

    await expect(unVerifyAnswer(id)).rejects.toThrow(NotFoundError);
  });

  it("Should throw error when listing answers with invalid question ID", async () => {
    let p = {
      userId: 1,
      page: 1,
    };

    mockAxios
      .onGet(`/answers/${p.userId}?page=${p.page}`)
      .reply(HTTPStatusCodes.NOT_FOUND);

    await expect(getByOwner(p)).rejects.toThrow(NotFoundError);
  });
});
