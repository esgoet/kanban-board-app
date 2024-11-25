import {useGetBoardByIdQuery} from "../api/apiSlice.ts";
import {useParams} from "react-router-dom";
import Column from "./Column.tsx";
import StatusHandler from "../api/StatusHandler.tsx";

export default function BoardPage() {
    const {boardId} = useParams();
    const {data: board, isLoading, isSuccess, isError, error} = useGetBoardByIdQuery(boardId!);


    return (
        <StatusHandler isLoading={isLoading} isError={isError} isSuccess={isSuccess} error={error}>
            <h2>{board?.name}</h2>
            {board?.columns.map((column) => (
                <Column key={column.id} {...column} />
            ))}
        </StatusHandler>
    );
};