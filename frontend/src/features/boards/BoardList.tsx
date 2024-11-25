import {useGetBoardsQuery} from "../api/apiSlice.ts";
import {Link} from "react-router-dom";
import StatusHandler from "../api/StatusHandler.tsx";

export default function BoardList() {
    const {data: boards = [], isLoading, isSuccess, isError, error} = useGetBoardsQuery();

    return (
        <StatusHandler isLoading={isLoading} isError={isError} isSuccess={isSuccess} error={error}>
            {boards.map((board) => (
            <Link to={`/boards/${board.id}`} key={board.id}>{board.name}</Link>
            ))}
        </StatusHandler>
    );
};