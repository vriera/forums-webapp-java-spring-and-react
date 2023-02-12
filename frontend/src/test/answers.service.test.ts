import { createAnswer, vote, deleteVote, getByOwner, unVerifyAnswer, verifyAnswer } from "../services/answers";
import { api as mockApi} from "../services/api";
import { NotFoundError } from "../models/ErrorTypes";

describe("AnswersService", () => {
  
  it("Should create answer when given a valid question ID", async () => {
    const mockCreateAnswer = jest.fn().mockReturnValue({ status: 201 });
    mockApi.post = mockCreateAnswer;
    const answer = { body: "answer" };
    const idQuestion = 1;
    await createAnswer(answer, idQuestion);
    expect(mockCreateAnswer).toHaveBeenCalledWith(`/answers/${idQuestion}`, {
      body: answer,
    });
  });

  it("Should throw error when creating answer with invalid question ID", async () => {
    mockApi.post = jest.fn().mockReturnValue({ status: 404 });
    const answer = { body: "answer" };
    const idQuestion = -1;
    await expect(createAnswer(answer, idQuestion)).rejects.toThrow(NotFoundError);
  });

  it("Should throw error when voting with invalid answer ID", async () => {
    mockApi.put = jest.fn().mockReturnValue({ status: 404 });
    const idUser = 1;
    const id = -1;
    const voteValue = true;
    await expect(vote(idUser, id, voteValue)).rejects.toThrow(NotFoundError);
  });

  it("Should throw error when deleting vote with invalid answer ID", async () => {
    mockApi.delete = jest.fn().mockReturnValue({ status: 404 });
    const idUser = 1;
    const id = -1;
    await expect(deleteVote(idUser, id)).rejects.toThrow(NotFoundError);
  });

  it("Should throw error when verifying answer with invalid answer ID", async () => {
    mockApi.post = jest.fn().mockReturnValue({ status: 404 });
    const id = -1;
    await expect(verifyAnswer(id)).rejects.toThrow(NotFoundError);
  });

  it("Should throw error when unverifying answer with invalid answer ID", async () => {
    mockApi.delete = jest.fn().mockReturnValue({ status: 404 });
    const id = -1;
    await expect(unVerifyAnswer(id)).rejects.toThrow(NotFoundError);
  });

  it("Should throw error when listing answers with invalid question ID", async () => {
    mockApi.get = jest.fn().mockReturnValue({ status: 404 });
    
    let p = {
      requestorId: 1,
      page: 1,
    }

    await expect(getByOwner(p)).rejects.toThrow(NotFoundError);
  });


});