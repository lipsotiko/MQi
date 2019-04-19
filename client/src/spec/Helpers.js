export async function forIt(ms) {
    let timeout = ms ? ms : 10;
    return new Promise(resolve => setTimeout(resolve, timeout));
}