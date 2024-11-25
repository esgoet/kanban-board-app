import {createApi, fetchBaseQuery} from "@reduxjs/toolkit/query/react";
import {Board, Task} from "../../types/apiTypes.ts";

export const apiSlice = createApi({
    reducerPath: 'api',
    baseQuery: fetchBaseQuery({baseUrl: '/api'}),
    endpoints: builder => ({
        getTasks: builder.query<Task[], void>({
            query: () => '/tasks',
        }),
        getBoards: builder.query<Board[], void>({
            query: () => '/boards',
        }),
        getBoardById:  builder.query<Board, string>({
            query: boardId => `/boards/${boardId}`,
        }),
    }),
})

export const { useGetTasksQuery, useGetBoardsQuery, useGetBoardByIdQuery } = apiSlice;