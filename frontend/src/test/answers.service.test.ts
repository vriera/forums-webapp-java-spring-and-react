import {createAnswer, deleteVote, getByOwner, unVerifyAnswer, verifyAnswer, vote,} from "../services/answers";
import {HTTPStatusCodes, NotFoundError} from "../models/HttpTypes";
import {api} from "../services/api";
import MockAdapter from "axios-mock-adapter";

describe("AnswersService", () => {
    let mockAxios = new MockAdapter(api);

    afterEach(() => {
        mockAxios.reset();
    });

    it("Should create answer when given a valid question ID", async () => {
        const answer = "This is a test answer";
        const idQuestion = 1;

        // Simulate a successful creation with HTTPStatusCodes.CREATED
        mockAxios.onPost(`/answers`).reply(HTTPStatusCodes.CREATED);
        let spy = jest.spyOn(api, "post");

        await createAnswer(answer, idQuestion);

        expect(spy).toHaveBeenCalledWith(`/answers`, {
            questionId: idQuestion,
            body: answer,
        });
    });

    it("Should throw error when creating answer with invalid question ID", async () => {
        const answer = "This is a test answer";
        const idQuestion = -1;

        // Simulate a NOT_FOUND response
        mockAxios.onPost(`/answers`).reply(HTTPStatusCodes.NOT_FOUND, {});

        let spy = jest.spyOn(api, "post");

        try {
            await createAnswer(answer, idQuestion);
        } catch (e) {
            expect(e).toBeInstanceOf(NotFoundError);
        }

        expect(spy).toHaveBeenCalledWith(`/answers`, {
            questionId: idQuestion,
            body: answer,
        });
        expect(spy).toHaveBeenCalledTimes(1);
    });


    it("Should throw error when voting with invalid answer ID", async () => {
        const idUser = 1;
        const id = -1;
        const voteValue = true;

        mockAxios.onPut(`/answers/${id}/votes/users/${idUser}?vote=${voteValue}`).reply(HTTPStatusCodes.NOT_FOUND);

        await expect(vote(idUser, id, voteValue)).rejects.toThrow(NotFoundError);
    });

    it("Should throw error when deleting vote with invalid answer ID", async () => {
        const idUser = 1;
        const id = -1;

        mockAxios.onDelete(`/answers/${id}/votes/users/${idUser}`).reply(HTTPStatusCodes.NOT_FOUND);

        await expect(deleteVote(idUser, id)).rejects.toThrow(NotFoundError);
    });

    it("Should throw error when verifying answer with invalid answer ID", async () => {
        const id = -1;

        mockAxios.onPost(`/answers/${id}/verify/`).reply(HTTPStatusCodes.NOT_FOUND);

        try {
            await verifyAnswer(id);
        } catch (e) {
            expect(e).toBeInstanceOf(NotFoundError);
        }
    });

    it("Should throw error when unverifying answer with invalid answer ID", async () => {
        const id = -1;

        mockAxios.onDelete(`/answers/${id}/verify/`).reply(HTTPStatusCodes.NOT_FOUND);

        await expect(unVerifyAnswer(id)).rejects.toThrow(NotFoundError);
    });

    it("Should throw error when listing answers with invalid question ID", async () => {
        let p = {
            userId: 1,
            page: 1,
        }

        mockAxios.onGet(`/answers/${p.userId}?page=${p.page}`).reply(HTTPStatusCodes.NOT_FOUND);

        await expect(getByOwner(p)).rejects.toThrow(NotFoundError);
    });

});
