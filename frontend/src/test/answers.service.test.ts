import {
  createAnswer,
  vote,
  deleteVote,
  getByOwner,
  unVerifyAnswer,
  verifyAnswer,
} from "../services/answers";
import { NotFoundError } from "../models/HttpTypes";
import { api } from "../services/api";
import MockAdapter from "axios-mock-adapter";

describe("AnswersService", () => {
  let mockAxios = new MockAdapter(api);

  afterEach(() => {
    mockAxios.reset();
  });

  it("Should create answer when given a valid question ID", async () => {
    const answer = "This is a test answer";
    const idQuestion = 1;

    mockAxios.onPost(`/answers/${idQuestion}`).reply(201);
    let spy = jest.spyOn(api, "post");    

    await createAnswer(answer, idQuestion);

    expect(spy).toHaveBeenCalledWith(`/answers/${idQuestion}`, {
      body: answer,
    });
  });

  
  it("Should throw error when creating answer with invalid question ID", async () => {
    const answer = "answer";
    const idQuestion = -1;

    mockAxios.onPost(`/answers/${idQuestion}`).reply(404);

    try {
      await createAnswer(answer, idQuestion);
    }
    catch (e) {
      expect(e).toBeInstanceOf(NotFoundError);
    }

    // await expect(createAnswer(answer, idQuestion)).rejects.toThrow(NotFoundError);
    expect(jest.spyOn(api, "post")).toHaveBeenCalledWith(`/answers/${idQuestion}`, {
      body: answer,
    });
    expect(jest.spyOn(api, "post")).toHaveBeenCalledTimes(1);
  });

  it("Should throw error when voting with invalid answer ID", async () => {
    const idUser = 1;
    const id = -1;
    const voteValue = true;

    mockAxios.onPut(`/answers/${id}/votes/users/${idUser}?vote=${voteValue}`).reply(404);

    await expect(vote(idUser, id, voteValue)).rejects.toThrow(NotFoundError);
  });

  it("Should throw error when deleting vote with invalid answer ID", async () => {
    const idUser = 1;
    const id = -1;

    mockAxios.onDelete(`/answers/${id}/votes/users/${idUser}`).reply(404);

    await expect(deleteVote(idUser, id)).rejects.toThrow(NotFoundError);
  });

  it("Should throw error when verifying answer with invalid answer ID", async () => {
    const id = -1;

    mockAxios.onPost(`/answers/${id}/verify/`).reply(404);

    try{
      await verifyAnswer(id);
    }
    catch (e) {
      expect(e).toBeInstanceOf(NotFoundError);
    }
  });

  it("Should throw error when unverifying answer with invalid answer ID", async () => {
    const id = -1;

    mockAxios.onDelete(`/answers/${id}/verify/`).reply(404);

    await expect(unVerifyAnswer(id)).rejects.toThrow(NotFoundError);
  });

  it("Should throw error when listing answers with invalid question ID", async () => {    
    let p = {
      requestorId: 1,
      page: 1,
    }

    mockAxios.onGet(`/answers/${p.requestorId}?page=${p.page}`).reply(404);

    await expect(getByOwner(p)).rejects.toThrow(NotFoundError);
  });

});
